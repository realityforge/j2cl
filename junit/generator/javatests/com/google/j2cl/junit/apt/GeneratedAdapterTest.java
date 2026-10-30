/*
 * Copyright 2026 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.j2cl.junit.apt;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the generated Java adapter. */
@RunWith(JUnit4.class)
public final class GeneratedAdapterTest {
  @Rule public TemporaryFolder output = new TemporaryFolder();

  @Test
  public void headerCompilesWithDoclint() throws Exception {
    TestClass testClass =
        TestClass.builder()
            .packageName("example")
            .simpleName("ExampleTest")
            .qualifiedName("example.ExampleTest")
            .testConstructor(TestConstructor.builder().numberOfParameters(0).build())
            .testMethods(ImmutableList.of())
            .beforeMethods(ImmutableList.of())
            .afterMethods(ImmutableList.of())
            .beforeClassMethods(ImmutableList.of())
            .afterClassMethods(ImmutableList.of())
            .beforeParamMethods(ImmutableList.of())
            .afterParamMethods(ImmutableList.of())
            .isJUnit3(false)
            .parameterizedDataMethod(Optional.empty())
            .parameterizedFields(ImmutableList.of())
            .build();
    String source =
        new VelocityRenderer(getClass())
            .renderTemplate("JsUnitAdapter.vm", Map.of("testClass", testClass));
    String className = testClass.jsUnitAdapterQualifiedClassName();
    JavaFileObject adapter = sourceFile(className, source);
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertThat(compiler).isNotNull();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    try (StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null)) {
      JavacTask task =
          (JavacTask)
              compiler.getTask(
                  null, files, diagnostics, List.of("-proc:none"), null, List.of(adapter));
      CompilationUnitTree unit = Iterables.getOnlyElement(task.parse());
      int packageEnd =
          Math.toIntExact(
              Trees.instance(task).getSourcePositions().getEndPosition(unit, unit.getPackage()));
      // Javac checks the package documentation only when the unit declares a type.
      JavaFileObject header =
          sourceFile(
              className,
              source.substring(0, packageEnd)
                  + "\n/** Triggers documentation checks. */\ninterface HeaderProbe {}\n");
      boolean succeeded =
          compiler
              .getTask(
                  null,
                  files,
                  diagnostics,
                  List.of(
                      "-proc:none", "-Xdoclint:all", "-Werror", "-d", output.getRoot().toString()),
                  null,
                  List.of(header))
              .call();
      assertWithMessage("Compiler diagnostics: %s", diagnostics.getDiagnostics())
          .that(succeeded)
          .isTrue();
      assertThat(diagnostics.getDiagnostics()).isEmpty();
    }
  }

  private static JavaFileObject sourceFile(String className, String source) {
    return new SimpleJavaFileObject(
        URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE) {
      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return source;
      }
    };
  }
}

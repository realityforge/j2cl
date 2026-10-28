# Peter Donald's J2CL fork

This fork collects independently reviewable changes for [google/j2cl](https://github.com/google/j2cl).
The default branch holds fork maintenance instructions. `integration` contains the downstream patches
rebased onto current upstream. Its history changes during refreshes, so pin consumers to an exact commit.
`upstream` contains upstream history only; `feature/*` branches retain individual changes for later
upstream submission.

## Prompt for a consolidation batch

Copy the prompt below into a coding agent with access to the relevant downstream repositories.

```text
Fork: https://github.com/realityforge/j2cl
Upstream: https://github.com/google/j2cl

Consolidate useful downstream J2CL patches into this existing fork. This task authorizes
implementation, tests, commits, pushes, and creation and merge of feature PRs inside the fork.
Do not submit upstream PRs or migrate downstream repositories. Preserve existing upstream
submissions, unrelated branches, and uncommitted user changes.

1. Read repository instructions and verify Git remotes and GitHub fork metadata. Discover
   upstream's default branch. Create or fast-forward the fork's upstream branch to its current
   commit, then freeze that exact baseline for this batch. Explain divergent history before
   changing it. Inspect existing feature branches, integration history, and open and merged PRs;
   reuse suitable work rather than duplicating it. Record the current remote heads before rewriting
   integration or upstream-discussion. Preserve existing feature branches and unrelated refs.

2. Audit the supplied downstream checkouts, relevant managed worktrees, GitHub default branches,
   working branches, and active PRs. Follow dependency declarations to additional sources.
   Record exact revisions, uncommitted differences, patch application order, and every distinct
   patch hunk. Inspect patch contents, consumers, tests, and history to establish motivation;
   filenames and the absence of direct callers are insufficient evidence.

3. Give every hunk a disposition: implement in a named feature; already satisfied upstream
   with a containing revision and behavioral evidence; replace with supported configuration;
   obsolete with evidence; deferred with the unresolved issue and affected capability; or
   owned by another upstream repository. A closed PR need not be merged, and a merged fix
   need not be in a consumer's pinned release. Keep the detailed provenance record private.

4. Present a partition by independently useful behavior, then implement. Deduplicate equivalent
   fixes, compare variants, split unrelated concerns, and keep inseparable behavior together.
   Prefer existing configuration and extension points. Add public interfaces only for verified
   callers. Keep consumer policy and application data downstream. Ask only for material scope
   changes, costly migration choices, or conflicts with the required branch model.

5. Start each feature/<descriptive-name> directly from the frozen baseline. Reproduce its
   problem and add focused, self-contained regression coverage where appropriate. Demonstrate
   failure before and success after where practical, and test each feature independently.
   Inspect the complete diff before committing. Preserve relevant authorship and licensing.
   Explain the public problem, motivation, behavior, validation, and limitations in commits
   and PRs without private names, paths, links, or application fixtures.

6. Initialize integration at exactly the baseline when absent. Otherwise rebase its existing patches
   onto the baseline with --rebase-merges, preserving patch boundaries and merge topology. Validate
   the rebased patches before publishing integration with an exact
   --force-with-lease=refs/heads/integration:<verified-remote-SHA>. If the lease fails, fetch and
   reconcile the concurrent change before retrying; never overwrite an unexamined remote update.
   Push each validated feature and open a PR in realityforge/j2cl with that feature as head and
   integration as base. Review its diff and required checks, then merge with a merge commit.
   Retain feature branches as provenance even when integration rebases change the corresponding SHAs.
   Keep features independent: do not merge integration into them or integrate new patches by direct
   pushes, cherry-picks, squash merges, or rebase merges. If features cannot be independent, explain
   the conflict and recommend a coherent grouping before departing from this topology.

7. Test the combined integration commit, including representative downstream requirements in
   isolated consumers where useful, without changing downstream tracked configuration. Separate
   baseline failures from regressions. Check that every audited hunk has a disposition, every
   integrated feature has a merged PR, and every retained patch is present or has an evidenced
   disposition after rebasing. Fetch upstream's default branch again before final publication; if
   it advanced, refresh integration and repeat the affected validation. When the task includes
   upstream-discussion, move it to the exact tested integration commit, using an exact
   --force-with-lease=refs/heads/upstream-discussion:<verified-remote-SHA> when history was rewritten.
   Update an existing upstream PR description only within the task's publication authority.
   Remove temporary scaffolding after preserving useful evidence privately. Report the baseline,
   feature branches and PRs, dropped and deferred requirements, cross-project dependencies,
   exact tested commits and checks, unavailable verification, and final immutable integration SHA.
   Report archive checksums only if actually generated and verified.

```


package flags.chrome

/**
 * Aliases the promise-based `browser.*` extension API to Chrome's `chrome.*`.
 *
 * We code against `browser.*`, which Firefox defines natively. Chrome (MV3)
 * exposes the same promise-based APIs under `chrome` and defines no `browser`,
 * so on Chrome we point `browser` at `chrome`; on Firefox this is a no-op.
 *
 * Must run before any `browser.*` access, so call it as the **first line** of
 * each entry point's `main()`, and keep module-level initializers that touch
 * `browser.*` (e.g. the stores) `by lazy` so they don't run before it.
 *
 * TODO: once `kotlin.js.EagerInitialization` graduates from experimental,
 * replace this function and its call sites with a single eager top-level shim
 * here — which runs at module load before dependents initialize, needing no
 * call sites and no `by lazy`:
 *
 *     @OptIn(ExperimentalStdlibApi::class)
 *     @EagerInitialization
 *     private val browserShim: Unit =
 *         js("if (typeof globalThis.browser === 'undefined') globalThis.browser = globalThis.chrome;")
 *             .unsafeCast<Unit>()
 */
fun ensureBrowserNamespace() {
    js("if (typeof globalThis.browser === 'undefined') { globalThis.browser = globalThis.chrome; }")
}

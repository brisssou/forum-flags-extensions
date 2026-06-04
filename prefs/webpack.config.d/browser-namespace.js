// We code against the promise-based `browser.*` extension API (native in
// Firefox). Chrome (MV3) exposes the same promise-based APIs under `chrome` and
// defines no `browser`, so alias it. Prepended (raw banner) ahead of module
// init, since the bundle's eager top-level initializers touch `browser.*`.
const webpack = require("webpack");
config.plugins = config.plugins || [];
config.plugins.push(
    new webpack.BannerPlugin({
        banner: "if (typeof globalThis.browser === 'undefined') { globalThis.browser = globalThis.chrome; }",
        raw: true,
        entryOnly: true,
    }),
);

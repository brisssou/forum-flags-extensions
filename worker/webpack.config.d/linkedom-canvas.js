// linkedom declares `canvas` as a peer dependency, used only for image /
// <canvas> rendering, which the service worker never does. It isn't installed,
// so resolve it to an empty module instead of failing with "Can't resolve
// 'canvas'".
config.resolve = config.resolve || {};
config.resolve.fallback = config.resolve.fallback || {};
config.resolve.fallback.canvas = false;

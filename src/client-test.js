
const keys = require('ssb-keys').loadOrCreateSync("/root/.ssb/secret")

const config = require('ssb-config/inject')('ssb', {
  path: "/root/.ssb",
  keys: keys,
  caps: {
    shs: process.env.SBOT_SHS,
    sign: process.env.SBOT_SIGN
  }
});

const ssb =
  require('scuttlebot')
  .use(require('.'))
  (config)

ssb.ssbIgo.getTotal()

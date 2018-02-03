
require("source-map-support").install({

})

const inject = require('./target/js/inject')

module.exports = ssb_igo.core.plugin
module.exports.protocol = ssb_igo.core.protocol

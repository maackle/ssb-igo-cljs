
const tape = require('tape')
const ssbIgoDb = require('../')
const Playbook = require('scuttle-playbook').use(ssbIgoDb)
const {step} = Playbook

const trace = x => (console.log(x), x)

const goodies = sbot => [sbot.ssbIgoDb, ssbIgoDb.protocol]

const terms = {
  size: 19,
  komi: 5.5,
  handicap: 0
}
// tape("Can expire your own request but not another's", w => {

const numResultsEquals = t => (call, num) => done => {
  call((_, m) => {
    t.equal(Object.keys(m).length, num)
    done()
  })
}

tape("Can expire your own request but not another's", t => {
  Playbook(sbot => (ana, burt) => {
    const [api, m] = goodies(sbot)

    return [
      step.message(ana, 'request', m.requestMatch(terms)),
      step.test(numResultsEquals(t)(api.getRequests, 1)),

      step.message(burt, refs => m.expireRequest()),
      step.test(numResultsEquals(t)(api.getRequests, 1)),

      step.message(ana, refs => m.expireRequest()),
      step.test(numResultsEquals(t)(api.getRequests, 0)),
    ]
  }, t.end)
})

tape("Can withdraw your own offer but not another's", t => {
  Playbook(sbot => (ana, burt) => {
    const [api, m] = goodies(sbot)

    return [
      step.message(ana, 'offer', m.offerMatch(terms, burt.id, true)),
      step.test(numResultsEquals(t)(api.getOffers, 1)),

      step.message(burt, refs => m.withdrawOffer(refs.offer.key)),
      step.test(numResultsEquals(t)(api.getOffers, 1)),

      step.message(ana, refs => m.withdrawOffer(refs.offer.key)),
      step.test(numResultsEquals(t)(api.getOffers, 0)),
    ]
  }, t.end)
})

tape("Can only accept your own match")
tape("Requests")

const startGameScript = sbot => (jack, jill) => {
  const [api, m] = goodies(sbot)
  return [
    step.message(jack, 'offer', m.offerMatch(terms, jill.id, true)),
    step.message(jill, refs => m.acceptMatch(refs.offer.key)),
  ]
}

tape("Offer is removed when starting a game", t => {
  Playbook(sbot => (ana, burt) => {
    const [api, m] = goodies(sbot)

    return [].concat(
      startGameScript(sbot)(ana, burt), [
        step.test(numResultsEquals(t)(api.getOffers, 0)),
        step.test(numResultsEquals(t)(api.getGames, 1)),
      ]
    )
  }, t.end)
})

// w.end()
// })

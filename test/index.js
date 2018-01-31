const tape = require('tape')
const ssbIgoDb = require('../')
// const scuttleTestbot = require('scuttle-testbot').use(ssbIgoDb)
const scuttlebot = require('scuttlebot').use(ssbIgoDb)

const flumeIndexName = name => `igoDb_${name}`


const withException = fn => (err, data) => {
  if (err) { throw new Error(err) }
  else { fn(data) }
}

const runPlaybook = (makePlaybook, andThen) => {

  const sbot = scuttlebot({ temp: true })

  const feeds = (new Array(makePlaybook.length)).fill().map(() => sbot.createFeed())
  const playbook = makePlaybook(...feeds)

  const runPlay = (playNum) => {
    if (playNum >= playbook.length) {
      andThen(sbot)
      // sbot.close()
      return
    }
    const {from, data, test} = playbook[playNum]
    const next = () => runPlay(playNum + 1)
    from.add(data, withException(() => {
      if (test.length === 2) {
        test(sbot, next)
      } else {
        test(sbot)
        next()
      }
    }))
  }

  runPlay(0)
}

for(let i=0; i < 3; i++) {


tape('it works', t => {

  const playbook = (alice, bob) => [
    {
      from: alice,
      data: {
        type: 'igo-chat',
        move: 'foo',
        text: 'yo! bobbo!'
      },
      test: (sbot, done) => {
        sbot.ssbIgoDb.getTotal((_, v) => t.equal(v, 1), done())
      }
    },
    {
      from: bob,
      data: {
        type: 'igo-chat',
        move: 'foo',
        text: 'heyyy al'
      },
      test: (sbot, done) => {
        sbot.ssbIgoDb.getTotal((_, v) => t.equal(v, 2), done())
      }
    }
  ]

  runPlaybook(playbook, (sbot) => {
    sbot.close(t.end)
    //sbot.ssbIgoDb.destroy(() => sbot.close(t.end))

  })

})

}

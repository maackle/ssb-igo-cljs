const tape = require('tape')
const ssbIgoDb = require('../')
const Playbook = require('ssb-playbook').use(ssbIgoDb)

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

  Playbook(playbook, t.end)

})

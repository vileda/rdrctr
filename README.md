[![Build Status](https://travis-ci.org/vileda/rdrctr.svg)](https://travis-ci.org/vileda/rdrctr) [![Coverage Status](https://coveralls.io/repos/vileda/rdrctr/badge.svg)](https://coveralls.io/r/vileda/rdrctr)

# rdrctr - a redirecter for domains

rdrctr is a simple tool to redirect domains
it is aware of subdomains and paths, for example `http://foo.bar.baz/oof/arb` redirects to `http://foo.bar.foo/oof/arb`

written in java ee 7, tested on wildfly, but it should run on any java ee 7 conform application server

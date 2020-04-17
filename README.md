Disclaimer
---------------------------
This isn't intended to be used, but rather a thought experiment.

Problem
---------------------------
Running 24/7 at ~2 wallet tests per second (about the most I can do before getting a 429 response code :( ), how long will it take to find a wallet with coin in it?

second = 2
minute = second * 60 = 120
hour = minute * 60 = 7200
day = hour * 24 = 172800
week = day * 7 = 120960
year = week * 52 = 62899200 = 6.28992 * 10^7

Assumptions
---------------------------
1. 42 million (4.2 * 10^7) active Bitcoin wallets (as of December 2019 [source](https://www.bitcoinmarketjournal.com/how-many-people-use-bitcoin/)).
1. All of which have a balance > 0.
1. No more wallets will be created... ever. 

Results
---------------------------
While there are 2^256 possible private keys, there are only 2^160 == (1.4615016 * 10^48) possible public addresses.

Probability of hitting a wallet:  1 in 3.4797658 * 10^40

Time to find an active wallet: (3.4797658 * 10^40) / ( 6.28992 * 10^7) = ~(5.5322894 * 10^32) = ~553228940000000000000000000000000 years

So if the universe is ~ [13.8 billion years old](https://en.wikipedia.org/wiki/Age_of_the_universe), how man universe lifetimes would it take to find a wallet?
(5.5322894 * 10 ^32) / (13.8 * 10^9) = (4.0089054 * 10^22) = ~40089054000000000000000 universe life times.

All of these are approximations, do your own math.

tl;dr
You'll never find a wallet with coin in it.

Credits to [https://blockchain.info](https://blockchain.info/), Google for the Base58 encoder, and https://en.wikipedia.org/wiki/RIPEMD
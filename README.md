Tusk
==============

Highly customized java code used to run the Walrus Network.

# Contents

* `Utilities` Utility code library (not a plugin) used by everything
  * `core` Utilities independent of Bukkit or Bungee
  * `bukkit` Bukkit utilities
  * `bungee` Bungee utilities
  * `parsing` Global parsing code and facets
* `Nerve` Connects and interacts with our backend to perform network services
  * `core` Base connectivity interfaces that don't require bukkit/bungee
  * `bukkit` Bukkit implementation of services
  * `bungee` Bukkit implementation of services
* `Ubiquitous` Common services that make use of the API and other things that should be active across the network
  * `core` Code that runs on bukkit/bungee that can use the backend API
  * `bukkit` Bukkit-specific code that runs on every server
  * `bungee` Bungee-specific code that runs on every server
* `Games` Code that runs all of our games (bukkit plugin)
  * `core` Core code across all games like generic modules and parsing
  * `octc` Classic OCN games like DTC and TDM
  * `uhc` UHC Solo/Teams games code
* `Tournament` Code used for running tournament-scope matches such as scrims
* `WelcomeMat` Lobby plugin

**NOTE:** The code in the nerve-core graph package is generated programmatically using the [graphql generation gem](https://github.com/Avicus/graphql_java_gen). After the generator is ran, all that should be done to that package is an IntelliJ code format.

# Coding Guidelines

* General
  * Use your judgment always. Any rule can be broken with a good reason. Don't follow a rule without understanding its purpose.
  * Write code for readability above all else. Always think about how another developer would work with your code.
  * Avoid repetitive code. Factor out the repetition, if there is a reasonable way to do so.
* Terms to Know
  * The OCN development team made some late adaptions of technologies and thus had to use less-than-optimal naming convetions to make everything work nicely, we don't have that problem so everything will actually be named what it is. This does lead to some different namings than in OCN, so read closely.
  * `GameRound` - Called "Match" in PGM. These hold all of the data specific for a single round of a game played on a `GameMap`. Name was changed in order not to interfere with the comparison APIs.
  * `Facet` - Called a "Module" in PGM. These are the per-round containers used to perform specific functions in the round environment. These are parsed from XML using `FacetParser`s. Name was changed in order to not interfere with Guice's `Module`s.
  * `ExternalComponent` - This is a new concept, but it fits in with the "facet module component" naming structure. These are the external JARs loaded by games core at load time, quite similar to a Bukkit plugin.
* Formatting
  * TODO
* Comments
  * Try to write code that is obvious enough so that it doesn't need to be explained with comments.
  * In places where a reader might be confused or miss something important, use comments to fill them in.
  * Don't put redundant or obvious things in comments or javadocs.
  * Ensure your IDE is not inserting any generated comments.
* Nulls
  * Strongly prefer `java.util.Optional` over `null`, generally speaking.
  * Use empty collections as nil values for collections.
  * Use `@Nullable` wherever nulls are allowed. Place it before the type, if possible.
  * Don't use `@Nonnull`. Assume anything (in our own code) without `@Nullable` is never null.
  * Use `checkNotNull` on constructor arguments.
* Structure
  * Design classes to do [one thing only](https://en.wikipedia.org/wiki/Single_responsibility_principle).
    If a class provides multiple services, break them down into seperate public interfaces and keep the class private.
  * Use `final` fields, and create immutable data types, wherever possible.
  * Don't create unnecessary getters and setters, only what is actually used.
  * No mutable static fields, collections, or any other static state (there are a few exceptions, such as caches and `ThreadLocal`s).
  * Getters don't have to start with `get`, but they can if you think it's important.
* Exceptions
  * Detect errors as early as possible, ideally at server startup. This applies to both user errors and internal assertions.
  * Only catch specific exceptions that you are expecting and can handle thoroughly. Don't hide exceptions that other handlers need to know about.
  * Avoid catching common exceptions like `IllegalArgumentException`, because it's hard to be certain where they come from.
    If you need to catch them, keep the code inside the `try` block as small as possible.
  * Don't catch all exceptions or try to handle internal errors for no particular reason.
  * Ensure that all errors are seen by a human who knows how to fix them:
* Concurrency
  * Avoid concurrency. It's hard, and we don't have a general solution for doing it safely.
  * A Bukkit server has a single "main thread" where most game logic runs, and multiple background threads for I/O and serialization.
    All `Event`s (except `AsyncEvent`s) and scheduled tasks (except async tasks) run on the main thread.
  * Never block the main thread on API calls, or any other I/O operation. Use `ListenableFuture`s and `FutureCallback`s to handle API results.
  * Don't use the Bukkit API, or any of our own APIs, from a background thread, unless it is explicitly allowed by the API.
  * A Bungee server is entirely multi-threaded. Handlers for a specific event run in sequence, but seperate events and tasks can run concurrently.
    This is one of the reasons we avoid doing things in Bungee.
* Localization
  * All in-game text must be localized.
  * To add a text string to the game, put the english template in one of the `.properties` files.
* Libraries
  * TODO
* Testing
  * TODO
* Logging
  * TODO

## Code Authors
Each file has an `@author` tag which denotes which member of the team wrote which piece of code. The main purpose of this technique is to provide support for other developers down the line when they need support. Instead of hunting around trying to figure out who wrote what, it's at the top of the file! Alsom be proud of the code you wrote!

The code does, however, contain code from two organizations, Overcast Network and Avicus Network. This code was grandfathered in from previous projects and is just around to provide a starting point for new code, so no new code from these places will ever be imported. 

Since it is difficult to determine which individual wrote what part of the organization code, they have been labeled with a comprehensive tag. The main authods from the organizations are as follows:

**Avicus:**
* Austin Mayes (@AustinLMayes)
* Keenan Thompson (@thekeenant)
* kashike (@kashike)
* Dalton Smith (@smitdalt)

**Overcast:**
* Tony Bruess (@tonybruess)
* Steve Anton (@steveanton)
* Yukon Vinecki (@YukonAppleGeek)
* Isaac Moore (@rmsy)
* Taylor Blau (@ttaylorr)
* Jedediah Smith (@jedediah)
* khazhyk (@khazhyk)
* Ashcon Partovi (@Electroid)
* Pablo Herrera (@Pablete1234)

## Workflow

* We use Git, with a typical [feature branch workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow)
* Trivial changes and emergency fixes can be merged straight to the master branch
* Any significant change requires a PR, and code review by at least one other developer.
  This applies indiscriminately to all developers. Everyone should have their code reviewed, and anyone can review anyone else's code.
* Once a change has been merged to master, it should be deployed ASAP so that problems can be found.
  Deploying several old changes at once just makes it harder to trace bugs to their source.
* Without automated tests, we rely heavily on user reports and Sentry alerts to discover regressions.
  Developers should be around for at least a few hours after their change is deployed, in case something breaks.

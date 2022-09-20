KCloak
======

*An opinionated Client implementation for Keycloak, the "k" stands for Kotlin*

![CI Status](https://github.com/4techteams/kcloak/actions/workflows/gradle.yml/badge.svg)

Back then, one dev in some software engineering team accidentally pronounced the famous identity provider
"Kaycloak". Another guy who knew that Kotlin users tend to prefix their libraries with "k" came up with "k-cloak". Thats
that... KCloak is, or at least started as a Kotlin Client for Keycloak, because the then-existing java implementation
was very "java-like". KCloak abstracts over all these internals, boilerplate, missing error handling, and sometimes not
well-documented calls, but still used the java client internally for maximum compatibility.

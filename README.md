# Emerson Professional Tools Public Maven Projects
Projects created by Emerson Professional Tools/The Ridge Tool Company (RIDGID) for public distribution via Maven.

These projects and modules contain generally useful, non-proprietary functionality implemented for internal usage of 
Emerson Professional Tools/The Ridge Tool Company and are being distributed publicly, "AS IS", under an Open Source 
License as a service to the community.

## Maven Group

The Maven Group ID for all of these projects is:

  * [com.ridgid.oss](https://oss.sonatype.org/#nexus-search;quick~ridgid.com.oss)

The Group ID that has been assigned ownership to Emerson/Ridge is:

  * [com.ridgid](https://oss.sonatype.org/#nexus-search;quick~ridgid.com)
  
So, in the future, other Group ID's can be published under com.ridgid if needed without requesting access to/ownership
of a new group id on Maven Central. For now though, all of the projects will use com.ridgid.oss as their Maven Group ID.

## Projects/Modules

  * **REST Web Services Doclet**
    * Artifact/Path: **com.ridgid.oss.restwebservices.doclet**
    * [README](com.ridgid.oss.restwebservices.doclet/README.md)
  * **REST Web Services API Version**
    * Artifact/Path: **com.ridgid.oss.spring.restwebservices.apiversion**
    * [README](com.ridgid.oss.spring.restwebservices.apiversion/README.md)

Further information about each project is available in their respective README files (see above).

## Licensing

All of these projects and modules are licensed under an approved Open Source License. In general, the BSD 3-Clause 
license will be used for all modules and projects. If any of the modules use any other license, it will be noted in 
their respective README.md files. In all cases, an Open Source license will be used.
    
## Publishing to Maven

The project and sub-modules are configured to deploy to [Maven Central](https://search.maven.org) via the 
[Sonatype Open Source Software Repository Hosting (OSSRH)](https://central.sonatype.org/pages/ossrh-guide.html#deployment). 

The [steps for correctly configuring the project to deploy to Maven Central via OSSRH](https://central.sonatype.org/pages/ossrh-guide.html#deployment)
available on Sontatype.org were followed in setting up these projects initially and do not need redone. 

In order to publish to Maven Central you must have configured your Maven settings 
appropriately and have a login for [OSSRH JIRA](https://issues.sonatype.org).

### JIRA User Name Set-Up & Publish Rights Assignment

You must first [create your JIRA login on the OSSRH JIRA Site](https://issues.sonatype.org/secure/Signup!default.jspa).

In order to add Emerson/Ridge Tool Company internal users as able to publish these artifacts to Maven, an existing 
Emerson internal user with publish rights must [open a JIRA ticket](https://issues.sonatype.org/secure/CreateIssue!default.jspa) 
and request that the new users who have already signed up for a JIRA account (see above) are added as publishers/owners 
of the corresponding Maven Central Group ID (ridge.com).

    NOTE: FOR SECURITY AND INTEGRITY OF THE PUBLISHED ARTIFACTS, ONLY EMERSON/RIDGE TOOL INTERNAL EMPLOYEES AND 
    CERTIFIED CONTRIBUTORS WILL BE GRANTED RIGHTS TO PUBLISH UPDATED OR NEW ARTIFACTS UNDER THE com.ridgid GROUP ID ON 
    MAVEN VIA OSSRH.

Once you are set-up with an OSSRH JIRA account and your account has been assigned publish rights you must then configure
you Maven settings.xml appropriately.

### Maven Configuration for Publishing

In order to publish you must first install "gpg" (GNU Privacy Guard) in order to be able to sign artifacts as
required by Maven Central Repository. You can get "gpg" via one of the following methods:

  * Install [cygwin](https://www.cygwin.com/)
  * Install [GPG for Windows](https://gnupg.org/ftp/gcrypt/binary/gnupg-w32cli-1.4.23.exe) from the 
    [GPG Download Site](https://gnupg.org/download/)
  * Install [GPG4Win](https://gpg4win.org/download.html) from [gpg4win.org](https://gpg4win.org/)
  
Once you have [installed GPG](https://central.sonatype.org/pages/working-with-pgp-signatures.html#installing-gnupg) you 
will need to set-up a GPG Public/Private Key Pair and publish your public key to the GPG key-server pools. 
[Instructions for this](https://central.sonatype.org/pages/working-with-pgp-signatures.html) are available on the 
Sonatype site. In particular, you will need to:

  * [Create a Public/Private Key Pair](https://central.sonatype.org/pages/working-with-pgp-signatures.html#generating-a-key-pair)
  * [Publish your public Key to the Key Servers](https://central.sonatype.org/pages/working-with-pgp-signatures.html#distributing-your-public-key)

You will then need to add your GPG pass-phrase information to your Maven settings.xml in your (USERHOME)/.m2 folder:

  ```xml
  <settings>
    ...
    <profiles>
      ...
      <profile>
        <id>ossrh</id>
        <activation>
          <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
          <gpg.executable>gpg</gpg.executable>
          <gpg.passphrase>(YOUR GPG PASS-PHRASE HERE)</gpg.passphrase>
        </properties>
      </profile>
      ...
    </profiles>
    ...
  </settings>
  ```

You will also need to add a server entry for the OSSRH Maven Repository:

  ```xml
  <servers>
    ...existing server entry...
    <server>
      <id>ossrh</id>
      <username>(YOUR OSSRH JIRA USER-NAME)</username>
      <password>(YOUR OSSRH JIRA PASSWORD)</password>
    </server>
  </servers>
  ```

You are now set-up to publish the artifacts to Maven Central via OSSRH.

### Publish Commands and Processes

The full process is documented for Maven under the [Apache Maven Performing a Snapshot Deployment](https://central.sonatype.org/pages/apache-maven.html#performing-a-snapshot-deployment)
and [Apache Maven Performing a Release Deployment with the Maven Release Plugin](https://central.sonatype.org/pages/apache-maven.html#performing-a-release-deployment-with-the-maven-release-plugin) 
instructions on Sonatype's site. In short, you must do the following:

  * To publish a SNAPSHOT:
  
    ```bash
    $> mvn clean deploy
    ```

    * You can then [view the deployed artifacts on the OSSRH Nexus Repository Manager](https://oss.sonatype.org/#nexus-search;quick~ridgid.com.oss)
    
  * To publish a RELEASE:
    * First, prepare the RELEASE:
    
      ```bash
      $> mvn release:clean release:prepare
      ```
      
      * This will update the version of all the modules to remove the "-SNAPSHOT" from the current version, create the
        release package, create the javadoc and source packages, and sign the packages as required by Maven. It will then
        commit this to Git and tag it with the release tag corresponding to the version. It will then update the versions
        of the modules to the next version number by incrementing the minor version number and then adding back the
        "-SNAPSHOT" to the versions. You may then commit and push to git to continue development on the next snapshot.
        
        ```
        NOTE: YOU SHOULD NOT MANUALLY UPDATE THE VERSION NUMBERS OF THE MODULES IN THE POM FILES. THE MAVEN RELEASE PLUGIN
              HANDLES THAT AUTOMATICALLY WHEN YOU INVOKE THE ABOVE MAVEN COMMAND TO CLEAN & PREPARE THE RELEASE.
        ```
  
    * Next, perform the RELEASE:
    
      ```bash
      $> mvn release:perform
      ```
      
      * You can then [view the deployed artifacts on the OSSRH Nexus Repository Manager](https://oss.sonatype.org/#nexus-search;quick~ridgid.com.oss)
  

# Vertx Deployment Manager #
## Motivations ##
Vert.x is a great platform! However, it is lacking in its module system. This isn't a bad thing; Vert.x exists as a low-level platform upon which applications with complex infrastructure can be built.

Enter, the Vertx Deployment Manager. Built as a Vertx Verticle, it exposes higher level functionality for managing the installation and running of modules.

## What does it do ##
While this isn't an exhaustive list, it represents the ideals and goals of what this Verticle strives to achieve.

* manage the deployment of modules installed in your application. Spin up new modules or decommission them at the click of a button.
* perform operations on an entire server cluster through a either a Web Interface, API, or CLI. Use the API to integrate with your existing infrastructure, or manage your cluster deployments through a command line client.
* easily manage your own private modules, and public modules, through the use of git repositories. Easily view any potential upgrades to your modules, and manage your versions.

## How do you use it ##

### Packaging your application ###
Your application must be packaged up into modules. You may have a "deployment verticle" that managed the deployment of your various modules. However, this is no longer required!

Place all your modules into the 'mods' folder of your working directory.

### Installing the Deployment Manager ###
Simply put the DeploymentManager.jar in your working directory.

### Create a Configuration File ###
If you want the deployment manager to deploy verticles upon launch, create a JSON configuration file. <<TODO: Not Supported Yet>>

### Running the Deployment Manager ###
Simply run the deployment manager as you would with normal verticles. (This assumes you have vertx in your PATH, and VERTX_MODS is default).

    vertx run DeploymentManager

## Known Issues ##

The following lists a bunch of conceptual issues which I have not thought of a potential solution yet (either due to technical limitations or lack of support in the Vertx core).

### Seamless Upgrade of Deployment Manager ###
While upgrading modules should be simple, I have not conceptualized a way of upgrading the core deployment manager itself. For v1 I expect that admins will simply do a rolling relaunch of each Deployment Manager across the server cluster.

### Messy Modules ###
At the moment, Vertx requires all modules to be placed in the same 'mods' folder. While this is alright, I would still like the ability to define multiple module directories together, so that I can install different sources of modules in different places (application modules, public modules, repository modules etc.). This will also allow me to store meta-data about each module (source, version etc.)


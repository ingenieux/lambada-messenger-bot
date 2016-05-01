# Lambada: The Game (as a Facebook Messenger Bot)

## What is it?

![Choose your Own Adventure](http://i.makeagif.com/media/5-01-2016/IBGBZ0.gif)

https://www.youtube.com/watch?v=JE_FWIicTXw

It is a story player (based on the [CYO format](http://danielstern.github.io/cyo/), but with XML Normalized), meant to be used with Facebook Messenger Platform

## Running Locally

 * Install ngrok and run:

```shell
$ ngrok http 8080
```

  * Copy env.properties.sample to env.properties - do not edit it yet -.

  * Then import into your IDE. Run:

```shell
$ mvn compile lambada:serve
```

  * Then, create a page and an application. Set the webhooks to:

```https://<your-ngrok-io>.ngrok.io/bot```

  * Now edit and add the token to env.properties

  * Restart Maven with the new setting

## Deploying

 * First, ensure you've ran ```aws configure``` (from awscli)

 * Then, set up the basic cloudformation stuff:

```shell
$ mvn cloudformation:push-stack
```

  * Then deploy:
  
```shell
$ mvn -Pdeploy deploy
```

  * Re-create the hook, this time using the resulting API Gateway endpoint URL e.g.: ```https://03q0sa93j9.execute-api.us-east-1.amazonaws.com/dev/bot```
  
  * Once valid, replace the Token on env.properties and redo the deployment from:

```shell
$ mvn -Pdeploy deploy
```

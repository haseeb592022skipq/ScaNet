# ScaNet (Android Network Scanner)


### Description

ScaNet is an Android Network Scanning app that provides complete network details, enumeration results along with performing vulnerability analysis in order to safeguard the network in the simplest way. ScaNet is a simple, user friendly and efficient Android application that will perform enumeration, network scanning and vulnerability analysis. Developed in Android studio (Java) it will have features like checking live hosts, finding open ports, router vulnerability check, network bandwidth details, Wake on LAN and vulnerability analysis etc. All this happens just on the phone without any security for privacy infringement. 

## steps to run lambda function build with cdk

- go to the stack file
- import lambda function from package @aws-cdk/aws-lambda
- write a lambda function
- create a handler function and pass that to lambda function
- specify runtime enviorment within function
- call the function within constructor
- run npm run watch to compile the ts files to js
- run cdk synth to translate the defined resources to AWS CloudFormation template
- run cdk deploy to deploy the stack to your AWS Account

## Helpful Links

- [aws-lambda module](https://docs.aws.amazon.com/cdk/api/v1/docs/aws-lambda-readme.html)
- [aws-amazon docs](https://aws.amazon.com/getting-started/hands-on/run-serverless-code/)

## Useful commands

- `npm run build` compile typescript to js
- `npm run watch` watch for changes and compile
- `npm run test` perform the jest unit tests
- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff` compare deployed stack with current state
- `cdk synth` emits the synthesized CloudFormation template

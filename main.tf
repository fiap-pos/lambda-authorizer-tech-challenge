terraform {
  backend "s3" {
    bucket = "tech-challenge-61"
    key    = "lambda-authorizer-tech-challenge/lambda.tfstate"
    region = "us-east-1"
  }
}

module "tech_challenge_lambda_authorizer" {
  source = "./terraform/"
}
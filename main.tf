terraform {
  backend "s3" {
    bucket = "vwnunes-tech-challenge-61"
    key    = "infra-eks-tech-challenge/lambda-authorizer.tfstate"
    region = "us-east-1"
  }
}

module "tech_challenge_lambda_authorizer" {
  source = "./terraform/"
}
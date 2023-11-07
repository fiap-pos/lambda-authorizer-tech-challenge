locals {
  target_jar_file_path = "target/lambda-authorizer-1.0-SNAPSHOT.jar"
  lambda_function_name = "techChallengeLambdaAuthorizerFunction"
  handler_path = "br.com.fiap.techchallenge.lambdaauthorizer.APIGatewayAuthorizerHandler"
}

terraform {
  backend "s3" {
    bucket = "tech-challenge-61"
    key    = "lambda-authorizer-tech-challenge/lambda.tfstate"
    region = "us-east-1"
  }
}

data "aws_iam_policy_document" "assume_lambda_authorizer_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "iam_for_lambda_authorizer" {
  name               = "iam_for_lambda_authorizer"
  assume_role_policy = data.aws_iam_policy_document.assume_lambda_authorizer_role.json
}

resource "aws_lambda_function" "tech_challenge_lambda_authorizer" {

  runtime       = var.java_runtime
  filename      = local.target_jar_file_path
  function_name = local.lambda_function_name
  role          = aws_iam_role.iam_for_lambda_authorizer.arn
  handler       = local.handler_path
  source_code_hash = filesha256(local.target_jar_file_path)


  # Buscar do SSM a url da aplicação de auth
  #environment {
  #  variables = {
  #    foo = "bar"
  #  }
  #  }
}
locals {
  target_jar_file_path = "target/lambda-authorizer-1.0-SNAPSHOT.jar"
  lambda_function_name = "techChallengeLambdaAuthorizerFunction"
  handler_path = "br.com.fiap.techchallenge.lambdaauthorizer.APIGatewayAuthorizerHandler"
}

data "aws_lb" "auth_lb" {
  name = var.auth_nlb_name
}

data "aws_vpc" "eks_vpc" {
  filter {
    name = "tag:Name"
    values = [var.eks_vpc_name]
  }
}

data "aws_subnets" "private_subnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.eks_vpc.id]
  }
  filter {
    name   = "tag:kubernetes.io/role/internal-elb"
    values = ["1"]
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

resource "aws_iam_role_policy_attachment" "iam_role_policy_attachment_lambda_vpc_access_execution" {
  role       = aws_iam_role.iam_for_lambda_authorizer.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_security_group" "lambda_authorizer_sg" {
  name_prefix = "lambda_authorizer_sg"
  vpc_id = data.aws_vpc.eks_vpc.id

  ingress {
    from_port = 0
    to_port = 65535
    protocol = "tcp"
  }

  egress {
    from_port = 0
    to_port = 65535
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lambda_function" "tech_challenge_lambda_authorizer" {

  runtime       = var.java_runtime
  filename      = local.target_jar_file_path
  function_name = local.lambda_function_name
  role          = aws_iam_role.iam_for_lambda_authorizer.arn
  handler       = local.handler_path
  source_code_hash = filesha256(local.target_jar_file_path)

  timeout = 300
  memory_size = 512

  vpc_config {
    subnet_ids         = data.aws_subnets.private_subnets.ids
    security_group_ids = [aws_security_group.lambda_authorizer_sg.id]
  }

  environment {
    variables = {
      AUTH_URL = "http://${data.aws_lb.auth_lb.dns_name}"
    }
  }

  depends_on = [
    data.aws_subnets.private_subnets,
    aws_security_group.lambda_authorizer_sg
  ]
}
variable "aws_region" {
  type = string
  description = "AWS Region"
  default = "us-east-1"
}

variable "environment" {
  type        = string
  description = "The environment to be built"
  default = "dev"
}

variable "java_runtime" {
  type = string
  description = "Lambda Java Runtime"
  default = "java17"
}

variable "application_tag_name" {
  type        = string
  description = "Value to put in application tag"
  default     = "tech-challenge-lambda-authorizer"
}


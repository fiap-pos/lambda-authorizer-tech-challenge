variable "aws_region" {
  type = string
  description = "AWS Region"
  default = "us-east-1"
}

variable "environment" {
  type        = string
  description = "The environment to be built"
  default     = "dev"
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

variable "auth_nlb_name" {
  type        = string
  description = "Name of the auth NLB"
  default     = "nlb-auth-service"
}

variable "eks_vpc_name" {
    type        = string
    description = "Name of the EKS VPC"
    default     = "vpc-tech-challenge-61-eks"
}

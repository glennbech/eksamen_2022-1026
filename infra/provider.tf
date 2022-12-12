terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.40.0"
    }
  }
  backend "s3" {
    bucket = "1026-terraform-state"
    key = "1026/apprunner-actions.state"
    region = "eu-north-1"
  }

}
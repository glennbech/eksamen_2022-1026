terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.40.0"
    }
  }
  backend "s3" {
    bucket = "analytics-1026"
    key = "1026/eksamen.state"
    region = "eu-north-1"
  }

}
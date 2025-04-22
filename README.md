# AWS Cost Visibility Platform

This project is a full-stack web application designed to help users onboard their AWS accounts and view resource usage and cost data in a clear and structured way.

## Features

- 🔐 IAM Role onboarding with validation
- 📊 AWS Resource fetching (EC2, RDS, Auto Scaling Groups)
- 📁 Modular, multi-step onboarding flow
- 🧾 CUR (Cost and Usage Report) setup
- 👥 Role-based access (Admin, ReadOnly, Customer)
- 📦 React + Redux front end with MUI components
- 🛠 Spring Boot back end with AWS SDK v2

## Tech Stack

- **Frontend**: React, Redux, Material UI, React Query
- **Backend**: Spring Boot, AWS SDK v2, Java
- **Persistence**: Redux Persist
- **Other**: GitHub, REST API, JSON-based DTOs

## Getting Started

1. Clone the repo
2. Set up your AWS credentials and roles
3. Run the Spring Boot backend
4. Start the React frontend
5. Onboard your AWS account and explore resource usage

## Folder Structure

- `frontend/`: React app
- `backend/`: Spring Boot app
- `src/main/resources/keys/`: Private and public keys (ignored in version control)

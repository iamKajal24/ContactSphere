# ContactSphere — Smart Contact Management System

A secure, responsive **Contact Management Web Application** built using Java and Spring Boot. ContactSphere enables users to manage contacts, authenticate securely, and upload profile images using Cloudinary.

## Features

- User registration and email/password authentication
- Google OAuth2 login
- Contact CRUD operations (Create, Read, Update, Delete)\*
- Contact search
- User dashboard and profile management
- Cloudinary profile image upload
- Email integration using Spring Mail and Mailtrap
- Server-side validation and Spring Security
- Responsive UI with Thymeleaf and Tailwind CSS

## Tech Stack

- **Backend:** Java 21, Spring Boot 4, Spring MVC
- **Security:** Spring Security, OAuth2 Client
- **Database:** MySQL, Spring Data JPA, Hibernate
- **Frontend:** Thymeleaf, HTML, Tailwind CSS, JavaScript
- **Integrations:** Cloudinary, Spring Mail, Mailtrap
- **Build Tool:** Maven

## Getting Started

### Prerequisites

Java 21, MySQL, Git, and Maven (or the included Maven Wrapper).

### 1. Clone the repository

```bash
git clone https://github.com/iamKajal24/ContactSphere.git
cd ContactSphere
```

### 2. Create the database

```sql
CREATE DATABASE contact_sphere_db;
```

### 3. Configure environment variables

Set the following variables before running the application:

`DB_USERNAME`, `DB_PASSWORD`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, `MAIL_USERNAME`, `MAIL_PASSWORD`.

`APP_BASE_URL` is optional and defaults to `http://localhost:8080`.

Configure OAuth callback URLs for your local application as required.

### 4. Run the application

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Maven installed:**

```bash
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080).

## Security

- Store credentials in environment variables.
- Never commit passwords, API secrets, or OAuth client secrets.
- Review database and logging settings before production deployment.

## Project Status

Core application features implemented. Production deployment is pending.

**Live Demo:** Coming soon.

## Author

**Kajal** — Java Backend Developer

## Live Demo

[Open ContactSphere](https://contactsphere-r3ny.onrender.com)

## GitHub Repository

[View Source Code](https://github.com/iamKajal24/ContactSphere)

---

_Note: Update, delete, and other feature claims should match the final tested implementation. Remove any feature that is not implemented._

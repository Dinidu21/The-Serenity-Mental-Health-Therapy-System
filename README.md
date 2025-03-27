# Project Management System Layered Architecture REFACTORED

## Overview

This **Project Management System** is designed to streamline project workflows, enhance team collaboration, and simplify task management. Inspired by JetBrains YouTrack, this tool provides a comprehensive suite of features including user authentication, task management, real-time notifications, and more. Built with modern technologies like **JavaFX**, **Java**, **CSS**, and **Maven**, this system is ideal for both small and large teams looking for a robust, secure, and scalable solution for managing projects.

# Feature List

## 1. **User Authentication and Security**
- **Secure Login System**: Enforces strong passwords and secures them with the **bcrypt hashing algorithm**.
- **Forgot Password Feature**: Users can reset passwords via **secure OTP (One-Time Password)** sent through email (limited to 3 attempts to prevent abuse).
- **Role-Based Access Control**: Provides different levels of access based on user roles (e.g., Admin, Project Manager, Developer, etc.).
- **User Deactivation**: Allows admin to deactivate users instead of deleting them, ensuring user data integrity.

## 2. **User Management**
- **Profile Management**: Users can update their profile details and upload a profile picture.
- **International Compatibility**: Supports phone number validation for **14 international countries** during the signup process.

## 3. **Dashboard & Visualization**
- **Modern Dashboard**: Stress-free, user-friendly design for better navigation and reduced cognitive load.
- **Visual Representation**: Includes **pie charts** and **bar charts** for easy insights into project and task statuses.
- **Task & Project Overview**: Real-time updates on ongoing projects and unresolved tasks.

## 4. **Project Management**
- **Task Tracking**: Allows tracking of task progress, time management, and unresolved tasks.
- **Milestone Evaluation**: Uses checklists to prioritize tasks effectively.
- **Notification System**:
    - Users are notified via email about new task assignments and team updates.
    - Automatic **deadline reminders** and **overdue notifications** sent 3 days before the due date.
- **Real-Time Features**: Includes real-time search and notifications for enhanced collaboration.

## 5. **Task Management**
- **Comprehensive Navigation**: Intuitive interface for project owners and managers to prioritize and monitor tasks.
- **Time Tracking**: Users can log and monitor their working hours.
- **Milestone Evaluation**: Clear indicators of high-priority tasks through checklists.

## 6. **Issue Tracking & QA**
- **Bug Reporting**: QA engineers can report issues, attach crash logs, and include attachments for developers.
- **Developer Notifications**: Developers receive instant updates when new issues or tasks are created.
- **Future Expansion**: Plans to incorporate real-time messaging for improved communication.

## 7. **Reporting**
- **Customizable Reports**: Project managers can generate reports in **CSV, XLSX, or PDF formats**.
- **Visualization Tools**: Easy-to-read bar charts showing each user's task count and progress.

## 8. **Timesheet Management**
- **User Timesheet**: Modern UI design to track individual working hours effortlessly.

## 9. **Scalability and Performance**
- **Scalable Architecture**: Designed to handle growing user and task loads without performance degradation.
- **Integration-Ready**: Built with future compatibility for third-party tool integration.
- **High Reliability**: Includes regular backups to prevent data loss.

## 10. **System Usability**
- **Cross-Platform Compatibility**: Accessible on both desktop and mobile devices.
- **Intuitive Design**: Minimal training required due to the user-friendly interface.

## 11. **Compliance & Data Integrity**
- **Data Protection**: Complies with relevant data protection regulations.
- **Data Integrity**: Ensures accuracy and prevents unauthorized data modifications.

## 12. **Maintainability**
- **Modular Design**: Facilitates updates and component replacements without system-wide impact.
- **Clear Documentation**: Comprehensive system documentation for easier maintenance.



## Screenshots 📸

###  Loading View

![image](https://github.com/user-attachments/assets/e878f43c-52f4-4d21-a897-834f7760945b)

###  Login view

![Screenshot 2024-11-20 195409](https://github.com/user-attachments/assets/22d1c6d6-c3eb-4264-aa35-8505476a4df6)

###  Sign-UP

![Screenshot 2024-11-20 195516](https://github.com/user-attachments/assets/8d679290-d6bf-4688-a60a-77ea0dba459d)

###  Forget Password

![Screenshot 2024-11-20 195418](https://github.com/user-attachments/assets/a7fe4d92-3f0b-4a50-a5d5-967ae3a3e608)
![Screenshot 2024-11-21 020700](https://github.com/user-attachments/assets/d98147a5-1230-45f3-8fef-988aede43206)
![Screenshot 2024-11-21 020742](https://github.com/user-attachments/assets/e0a3324e-a345-4c84-a5f0-e534285befd3)
![Screenshot 2024-11-21 020800](https://github.com/user-attachments/assets/6ab09b98-3051-4dcd-b46e-ff87f1e1593e)
![Screenshot 2024-11-21 020812](https://github.com/user-attachments/assets/d77c0dde-fa87-40ef-a96c-5289e800c73a)

###  Dashboard view

![Screenshot 2024-11-20 194552](https://github.com/user-attachments/assets/05c9df49-7c5b-4cd3-a82b-17757d168e5f)

###  Projets view

![Screenshot 2024-11-20 194559](https://github.com/user-attachments/assets/4cbb7a8b-d78f-4fc2-be5d-2240b3ef5d51)
![Screenshot 2024-11-20 194620](https://github.com/user-attachments/assets/46d609ce-e229-4d9c-8c1d-9c3baebf06ee)
![Screenshot 2024-11-20 194608](https://github.com/user-attachments/assets/6c023532-8567-4961-af7a-5a478a8404f4)

###  Task view

![Screenshot 2024-11-20 195207](https://github.com/user-attachments/assets/91abd31b-e0f7-4cf3-9607-cded8c8b4b92)
![Screenshot 2024-11-20 195145](https://github.com/user-attachments/assets/8d4ba50c-5b23-4549-a887-e10a0cb19a78)
![Screenshot 2024-11-20 195136](https://github.com/user-attachments/assets/cd0f56ad-71d0-498a-bd93-c169ab317942)
![Screenshot 2024-11-20 194730](https://github.com/user-attachments/assets/560e6610-2907-41bd-9db5-89470f3f7c2b)

###  Reports view

![Screenshot 2024-11-20 194717](https://github.com/user-attachments/assets/c2522fd8-f7e1-4bc1-aaed-4caba891c2d3)
![Screenshot 2024-11-20 194701](https://github.com/user-attachments/assets/b67a7e6b-dc94-4827-9b32-84dde02eb550)

###  Issues view

![Screenshot 2024-11-20 194654](https://github.com/user-attachments/assets/8def13a1-0e24-4056-b252-c15b1ebd1bb6)
![Screenshot 2024-11-20 194641](https://github.com/user-attachments/assets/3477aadb-713c-4a5a-8778-cf54eea255da)
![Screenshot 2024-11-20 194632](https://github.com/user-attachments/assets/137b62ed-977e-4143-b7a7-000052d98c81)

###  Timesheets view

![Screenshot 2024-11-20 195233](https://github.com/user-attachments/assets/91744210-f0c7-414c-9da8-d845c1607d17)
![Screenshot 2024-11-20 195225](https://github.com/user-attachments/assets/f1acc609-76b1-437c-9836-09eb0e36def2)

###  Dashboards view

![Screenshot 2024-11-20 195307](https://github.com/user-attachments/assets/3768dfd8-e63a-49a2-8434-8ed9c221c823)
![Screenshot 2024-11-20 195256](https://github.com/user-attachments/assets/adf40363-7e76-4ed1-9ed0-828ff4dbeb56)
![Screenshot 2024-11-20 195240](https://github.com/user-attachments/assets/ceba4fdf-ac1a-4f9d-8ecc-9206ccea81ac)

###  Settings view

![Screenshot 2024-11-20 195327](https://github.com/user-attachments/assets/51ff5d35-a5e3-405d-96dd-0384943348d0)

###  Notification view

![Screenshot 2024-11-20 195320](https://github.com/user-attachments/assets/b08a465f-89ab-41c3-b5ab-c17764a7cc46)

###  Profile view

![Screenshot 2024-11-20 195351](https://github.com/user-attachments/assets/a4f2e16d-4215-48bc-bc46-17782dda9695)
![Screenshot 2024-11-20 195334](https://github.com/user-attachments/assets/295c90d7-3413-4e87-b93f-3a749955715b)

## Technologies Used

- **JavaFX** for the graphical user interface.
- **Java** for core functionality.
- **CSS** for styling.
- **Maven** for project build automation.
---

For more, check out the [full set of screenshots](https://drive.google.com/file/d/1lieg7LxJEAHADKdvWKQ3g3nASAeVPapP/view?usp=sharing).

- Contact me at: [dinidusachintha3@gmail.com](mailto:dinidusachintha3@gmail.com)
- LinkedIn: [dinidu21](https://www.linkedin.com/in/dinidu21)

---

## Future Roadmap

- **Real-Time Messaging Integration**: Implement a real-time messaging system to enhance communication and collaboration across teams.
- **AI-Driven Predictive Analytics**: Leverage machine learning algorithms to provide insights and forecasts, helping teams predict project timelines, resource allocation, and task completion with high accuracy.
- **Advanced Machine Learning Capabilities**: Integrate advanced ML features to optimize project management tasks, such as workload balancing and risk identification.
- **Leverage Predictive Analytics**: Use AI-powered predictive analytics to improve decision-making and boost project success rates by 30%.
- **System Evolution**: Continue to evolve the system with cutting-edge technologies, ensuring long-term relevance and maintaining a competitive advantage.
- **AI ChatBot**: Introduce an AI-driven chatbot to automate routine queries and assist with task allocations, reducing administrative overhead by 40%.


## License

This project is licensed under a Commercial Licens - see the [LICENSE](https://github.com/Dinidu21/PMT/blob/main/LICENSE.md) file for details.

---

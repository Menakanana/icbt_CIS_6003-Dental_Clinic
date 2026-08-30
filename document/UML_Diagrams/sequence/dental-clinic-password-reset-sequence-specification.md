# Password Reset & Security Token Sequence Specification (UC-2)

## Overview
This specification details the interaction flow between actors, Presentation Controllers, Business Logic Services, Repositories, BCrypt Encoder, and Email Gateway during a staff password reset operation.

## Sequence Workflows
1. **Initiate Reset Request**: User inputs registered email $\rightarrow$ `AuthService` verifies existence $\rightarrow$ generates single-use token $\rightarrow$ dispatches email link.
2. **Execute Password Reset**: User clicks link $\rightarrow$ submits new password $\rightarrow$ `AuthService` checks token expiry & usage $\rightarrow$ encodes new password with BCrypt $\rightarrow$ updates database & consumes token.

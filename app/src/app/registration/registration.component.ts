import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-registration',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './registration.component.html',
  styleUrl: './registration.component.css'
})
export class RegistrationComponent {
  firstName = '';
  lastName = '';
  email = '';
  submitted = false;
  confirmationMessage = '';

  sendConfirmationCode(form: NgForm): void {
    this.submitted = true;

    if (form.invalid) {
      this.confirmationMessage = '';
      return;
    }

    this.confirmationMessage = 'Alle Pflichtfelder sind ausgefüllt.';
  }
}

import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/services/auth/auth.service';
import {  Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public mail:string = "";
  public pass:string = "";

  constructor(private auth:AuthService, private router:Router) { }

  ngOnInit() {

    if(this.auth.getUser()){
      console.log("logged in");
      this.router.navigate(["/doel"]);
    }
    else{
      console.log("not logged in");
      //niets doen, gebruiker laten inloggen
    }

  }



  public login(){
    this.auth.logIn(this.mail,this.pass).then(() => this.router.navigate(["/doel"]),
    error => alert(error.message)
  );
  }

}

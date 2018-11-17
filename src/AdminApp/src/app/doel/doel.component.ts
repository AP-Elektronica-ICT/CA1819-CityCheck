import { Component, OnInit } from '@angular/core';
import { latLng, LatLng, tileLayer } from 'leaflet';
import { AuthService } from 'src/services/auth/auth.service';
import { Router } from '@angular/router';
import { ThrowStmt } from '@angular/compiler';

@Component({
  selector: 'app-doel',
  templateUrl: './doel.component.html',
  styleUrls: ['./doel.component.scss']
})
export class DoelComponent implements OnInit {


  constructor(private auth:AuthService, private router:Router) { }

  ngOnInit() {


    if(this.auth.getUser()){
      console.log("logged in");
      //we zijn aangemeld, we mogen hier zijn.
    }
    else{
      console.log("not logged in");
      //niet ingelogd, eerst inloggen.
      this.router.navigate([("/login")]);
    }

  }


  options = {
    layers: [
      tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'City Check Locs' })
    ],
    zoom: 17,
    center: latLng(51.2289238, 4.4026316)
  };


}

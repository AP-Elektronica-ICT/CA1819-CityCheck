import { Component, OnInit } from '@angular/core';
import { latLng, tileLayer } from 'leaflet';
import { AuthService } from 'src/services/auth/auth.service';
import { Router } from '@angular/router';
import { ThrowStmt } from '@angular/compiler';
import { DataService, ILocRoot } from 'src/services/Data/data.service';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

@Component({
  selector: 'app-doel',
  templateUrl: './doel.component.html',
  styleUrls: ['./doel.component.scss']
})
export class DoelComponent implements OnInit {
  //TODO: duidelijk maken welke locatie geselecteerd is

  private allLocs:ILocRoot;

  private locsToShow:ILocRoot;
  private amountToShow:number = 5;
  private page:number = 0;


  constructor(private auth:AuthService, private router:Router, private data:DataService) { }

  ngOnInit() {


    if(this.auth.getUser()){
      console.log("logged in");
      //we zijn aangemeld, we mogen hier zijn.
    }
    else{
      console.log("not logged in");
      //niet ingelogd, eerst inloggen.
      //this.router.navigate([("/login")]);
    }

    this.data.getLocations().subscribe(r=> this.allLocs = r);
    

  }


  public options = {
    layers: [
      tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'City Check Locs' })
    ],
    zoom: 17,
    center: latLng(51.2289238, 4.4026316)
  };


  public center = latLng(51.2289238, 4.4026316);



  public pickLoc(loc:ILocRoot):void{
    //console.log(loc);

    //change center
    this.center = latLng(loc.locatie.lat,loc.locatie.long);
    //place marker


  }


}

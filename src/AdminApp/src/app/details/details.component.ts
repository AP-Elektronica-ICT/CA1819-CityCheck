import { Component, OnInit } from '@angular/core';
import { ILocRoot, DataService } from 'src/services/Data/data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit {

  private location:ILocRoot;

  //invulvelden locatie
  public titel:string = "";
  public newlat:number;
  public newlong:number;

  constructor(private route:Router, private data:DataService) { }

  ngOnInit() {

    var loc = this.data.getChosenLoc();

    //kijken if locatie
    console.log("checking");
    if(loc == undefined || loc.id <1){
      console.log("true");
      this.route.navigate(["/doel"]);
    }

    //gekozen locatie ophalen
    this.location = loc;
    this.location.id = loc.id;
    this.location.locatie = loc.locatie;
    this.location.titel = loc.titel;
    this.location.vragen = loc.vragen;

    //datafields invullen
    this.titel = this.location.titel;
    this.newlat = this.location.locatie.lat;
    this.newlong = this.location.locatie.long;





  }

}

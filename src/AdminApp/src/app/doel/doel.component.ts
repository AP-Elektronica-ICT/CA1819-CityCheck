import { Component, OnInit } from '@angular/core';
import { latLng, tileLayer, marker } from 'leaflet';
import { AuthService } from 'src/services/auth/auth.service';
import { Router } from '@angular/router';
import { ThrowStmt } from '@angular/compiler';
import { DataService, ILocRoot } from 'src/services/Data/data.service';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { DoelLocatie } from '../classes/DoelLocatie';
import { Locatie } from '../classes/Locatie';

@Component({
  selector: 'app-doel',
  templateUrl: './doel.component.html',
  styleUrls: ['./doel.component.scss']
})
export class DoelComponent implements OnInit {
  //TODO: duidelijk maken welke locatie geselecteerd is

  public allLocs:ILocRoot[];
  private totalLocs:number = 0;

  public page:number = 0;
  public searchStr:string = "";


  //invulvelden nieuwe locatie
  public titel:string = "";
  public newlat:number;
  public newlong:number;



  //Map variables---------------------------------------------------
  public options = {
    layers: [
      tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: 'City Check Locs' })
    ],
    zoom: 17,
    center: latLng(51.2289238, 4.4026316)
  };

  public center = latLng(51.2289238, 4.4026316);

  layers = [
    marker(this.center)
  ];
  //Map variables---------------------------------------------------


  constructor(private auth:AuthService, private router:Router, private data:DataService) { }

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

    //eerste locaties ophalen
    this.getLocations(this.page);
    
  }


  public getLocations(page?:number,naam?:string){
    this.data.getLocations(page, naam).subscribe( (r) => {
      this.allLocs = r;
      //this.totalLocs = r.length;


      //paging niet te hoog laten gaan als er gaan data meer is
      if(this.allLocs.length <=0 && this.searchStr == ""){
        this.page-=1;
        this.getLocations(this.page);
      }
      //-------------------------

     });
  }



  public search(){
    //zoeken volgens search string
    this.getLocations(null, this.searchStr);
    //page op 0 zetten aan het begin
    this.page = 0;
  }


  public paging(goUp:boolean){
    
    //nieuwe pagina nummer instellen
    if(goUp){
      //go next
      this.page +=1;
    } else {
      //go back
      this.page -=1;
    }

    //niet onder 0 gaan
    if(this.page <=0){
      this.page = 0;
    }

    //nieuwe get call uitvoeren op de huidige page status
    this.getLocations(this.page);

  }


  public backToStart(){
    this.page = 0;
    this.getLocations(this.page);
  }



  public addLocation():void{

    //locatie aanmaken
    var loc = new Locatie(this.newlat,this.newlong);
    //doellocatie aanmaken
    var doelloc = new DoelLocatie(this.titel,loc,null);
    //doellocatie posten
    this.data.postLocation(doelloc).subscribe(res =>{
      console.log(res);
      //this.getLocations();
    });
  }



  public pickLoc(loc:ILocRoot):void{
    //console.log(loc);

    //change center
    this.center = latLng(loc.locatie.lat,loc.locatie.long);
    //place marker
    this.layers = [
      marker(this.center)
    ];

  }


}

import { Component, OnInit } from '@angular/core';
import { ILocRoot, DataService } from 'src/services/Data/data.service';
import { Router } from '@angular/router';
import { Antwoord, Vraag } from '../classes/Vragen';

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


  //invulvelden vraag
  public newVraag:string = "";
  public newAntw1:string = "";
  public newAntw2:string = "";
  public newAntw3:string = "";
  public correct:number = 1;

  constructor(private route:Router, private data:DataService) { }

  ngOnInit() {

    this.location = this.data.getChosenLoc();

    //kijken if locatie
    //console.log(this.location);
    if(this.location == undefined || this.location.id <1){
      this.route.navigate(["/doel"]);
    }






  }



  public delete(){
    //Delete loc
    this.data.delLocation(this.location.id).subscribe();
    this.route.navigate(["/doel"]);
  }


  public edit(){
    //Edit loc
    this.data.editLocation(this.location.id,this.location).subscribe(res => console.log(res));
  }


  public getQ(){
    //Ophalen van alle vragen binnen deze loc
  }


  public addQ(){
    //Een nieuwe vraag toevoegen aan deze loc
    var correctNumb:number = this.correct -1;

    //De 3 Antwoorden aanmaken
    var loc1:Antwoord = new Antwoord(this.newAntw1,false);
    var loc2:Antwoord = new Antwoord(this.newAntw2,false);
    var loc3:Antwoord = new Antwoord(this.newAntw3,false);

    //Aanmaken van een antwoord object
    var newAntw:Antwoord[] = [loc1,loc2,loc3];

    newAntw[correctNumb].CorrectBool = true;

    //Aanmaken van een vraag object
    var newVrg = new Vraag(this.newVraag,newAntw);
    //Doorpushen van de vraag binnen deze locatie


    //Toevoegen van de vraag aan de local locatie => loc.vraag = ;



  }

}
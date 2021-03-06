import { Component, OnInit } from '@angular/core';
import { ILocRoot, DataService, Vragen } from 'src/services/Data/data.service';
import { Router } from '@angular/router';
import { Antwoord, Vraag } from '../classes/Vragen';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit {

  public location:ILocRoot;

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
    this.data.editLocation(this.location.id,this.location).subscribe(res => {
      //this.route.navigate(["/doel"]);
      alert("Edited");
    });
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

    this.data.postQuestion(this.location.id,newVrg).subscribe(r=>{
      if(r.returnWaarde == "Created"){
        this.refresh();
        //data weer clearen
      this.newVraag = "";
      this.newAntw1 = "";
      this.newAntw2 = "";
      this.newAntw3 = "";
      this.correct = 1;
      }
    });

  }


  public delQ(vraag:number){
    this.data.delQuestion(this.location.id, vraag).subscribe(r=> {
      if(r.returnWaarde == null){
        alert("Er is iets fout gelopen");
      } else
      if(r.returnWaarde != ""){
        //Deleted
        this.refresh();
      }
    });
  }


  private refresh(){
    this.data.getLocations(0,this.location.titel).subscribe(r=>{
      this.location = r[0];
    })
  }

}
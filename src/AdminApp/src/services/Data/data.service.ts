import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from "@angular/common/http";
import { DoelLocatie } from 'src/app/classes/DoelLocatie';
import { Vraag } from 'src/app/classes/Vragen';

@Injectable()
export class DataService {

  private url:string = "http://84.197.96.121/api/citycheck/";

  private chosenLoc:ILocRoot;


  constructor(private _http:HttpClient) {

   }

   //Ophallen en setten van een gekozen locatie om te gaan bewerken
   public setChosenLoc(loc:ILocRoot){
      this.chosenLoc = loc;
   }


   public getChosenLoc(){
      return this.chosenLoc;
   }
   

   //locaties ophalen
   getLocations(page:number, naam?:string): Observable<ILocRoot[]> {

    if(naam != null){
      //we zoeken op naam
      return this._http.get<ILocRoot[]>(this.url+"allDoelLocs?naam="+naam)
    }

    return this._http.get<ILocRoot[]>(this.url+"allDoelLocs?page="+page)
    
  }


  //nieuwe locatie posten
  postLocation(loc:DoelLocatie): Observable<DoelLocatie> {
    return this._http.post<DoelLocatie>(this.url+"addDoelLocs",loc)
    
  }


  //locatie verwijderen
  delLocation(id:number): Observable<string> {
    return this._http.delete<string>(this.url+"delDoelLocs/"+id)
    
  }


  //locatie editen
  editLocation(id:number, loc:DoelLocatie): Observable<ILocRoot> {
    return this._http.put<ILocRoot>(this.url+"editDoelLocs/"+id, loc)
    
  }


  //Vraag bij een locatie toevoegen
  postQuestion(id:number, vraag:Vraag): Observable<any> {

    return this._http.post<any>(this.url+"addDoelLocsQuestion/"+id,vraag)
    
  }


  //Vraag bij een locatie verwijderen
  delQuestion(id:number, vid:number): Observable<stringReturn> {

    return this._http.delete<stringReturn>(this.url+"dellDoelLocsQuestion/"+id+"/"+vid)
    
  }

}


//interfaces
  export interface ILocRoot {
    id: number;
    titel: string;
    locatie: Locatie;
    vragen: Vragen[];
  }
  
  export interface Vragen {
    id: number;
    vraagZin: string;
    antwoorden: Antwoorden[];
  }
  
  export interface Antwoorden {
    id: number;
    antwoordzin: string;
    correctBool: boolean;
  }
  
  export interface Locatie {
    id: number;
    lat: number;
    long: number;
  }


  export interface stringReturn {
    returnWaarde: string;
  }
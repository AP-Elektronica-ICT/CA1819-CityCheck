import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from "@angular/common/http";

@Injectable()
export class DataService {

    private url:string = "http://84.197.96.121/api/citycheck/";


  constructor(private _http:HttpClient) {

   }
   

   getLocations(): Observable<ILocRoot> {

    return this._http.get<ILocRoot>(this.url+"allDoelLocs")
    
  }





//   createAthlete(vnaam:string,anaam:string,natio:string,leeft:number,aantala:number,sportid:number, key:string){
//     return this._http.post(this.mainUrl+`athletes?key=`+key,{
//       "voornaam": vnaam,
//       "achternaam": anaam,
//       "nationaliteit": natio,
//       "leeftijd": leeft,
//       "aantalAwards": aantala,
//       "SportId": sportid
//   })
//   }




}


//interfaces


export interface ILocRoot {
    id: number;
    titel: string;
    locatie: Locatie;
    vragen?: any;
  }
  
  interface Locatie {
    id: number;
    lat: number;
    long: number;
  }


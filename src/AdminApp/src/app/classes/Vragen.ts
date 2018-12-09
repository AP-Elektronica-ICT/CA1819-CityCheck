export class Vraag {

    constructor(public VraagZin:string,public Antwoorden:Antwoord[]) {

    }

}



export class Antwoord {

    constructor(public Antwoordzin:string,public CorrectBool:boolean) {

    }

}
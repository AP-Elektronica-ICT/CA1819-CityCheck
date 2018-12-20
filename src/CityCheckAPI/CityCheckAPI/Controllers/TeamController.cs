using System;
using System.Collections.Generic;
using System.Linq;
using BussinessLayer.Methods;
using DataLayer;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Model;


[Route("api/citycheck")]
[ApiController]
public class TeamController : Controller
{

    private readonly CityCheckContext context;

    private TeamMethods teamMethods;


    public TeamController(CityCheckContext context)
    {
        this.context = context;

        this.teamMethods = new TeamMethods(context);
    }



    //new team in a game
    [HttpPost]
    [Route("teams/{gameId}")]
    public IActionResult addteam([FromBody] Team newTeam, int gameId)
    {
        Team team = teamMethods.addteam(newTeam, gameId);

        if (team != null)
            return Ok(team);
        else
            return NotFound();
                   
    }

    //get all teams from a game and their location
    [HttpGet]
    [Route ("currentgame/teams/{gameId}")]
    public IActionResult getTeams(int gameId)
    {
        List<Team> teams = teamMethods.getTeams(gameId);
        if (teams == null)
            return NotFound();
        return Ok(teams);
    }


    //update team
    //wrss nooit nodig
    [HttpPut]
    [Route("teams/{id}")]
    public IActionResult updateTeam([FromBody] Team update, int id)
    {
        var team = teamMethods.updateTeam(update, id);
        if (team == null)
            return NotFound();
        team = update;
        return Ok(team);
    }

    //delete team
    //wrss nooit nodig
    [HttpDelete]
    [Route("teams/{id}")]
    public IActionResult deleteTeam(int id)
    {
        var team = teamMethods.deleteTeam(id);
        if (team == 0)
            return NotFound();
        else {
            return Ok(team);
        }
    }


    //save teamloc
    [HttpPost]
    [Route("teams/{id}/{teamname}/huidigeLocatie")]
    public IActionResult SaveCurrentTeamLoc([FromBody] Locatie currentLoc, int id, string teamname)
    {
        //id is de gamecode
        //we gaan het team selecteren volgens de teamnaam
        var team = teamMethods.SaveCurrentTeamLoc(id,teamname,currentLoc);
        if (team == null)
            return NotFound();
        else
        {
            return Ok(team);
        }
    }



    //save team color
    [HttpPost]
    [Route("teams/{id}/{teamname}/teamcolor")]
    public IActionResult SaveTeamColor(int id, string teamname, [FromBody] int kleurcode)
    {
        //id is de gamecode

        var team = teamMethods.SaveTeamColor(id, teamname, kleurcode);


        if (team == null)
            return NotFound();
        else
        {
            return Created("Accepted:", team);
        }
    }



    //get all traces of all teams in a game
    [HttpGet]
    [Route("teams/{id}/trace")]
    public IActionResult getTeamTrace(int id)
    {
        //id is de gamecode

        List<Team> teams = teamMethods.getTeamTrace(id);
        if (teams == null)
            return NotFound();
        else
        {
            return Ok(teams);
        }

    }


    //Dit mag eigenlijk weg
    //add new point to the teamtrace
    [HttpPost]
    [Route("teams/{id}/{teamNaam}/trace")]
    public IActionResult setTeamTrace([FromBody] Locatie newLoc, int id, string teamNaam)
    {
        //id is de gamecode
        var traceadded = teamMethods.setTeamTrace(newLoc, id, teamNaam);

        if (!traceadded)
            //Geen team
            return NotFound();
        else
        {
            return Ok("Trace added");
        }

    }



    //score van een team in een game opvragen
    [HttpGet]
    [Route("teams/{id}/{teamName}/myscore")]
    public IActionResult getTeamScore(int id, string teamName)
    {
        //id is de gamecode
        var score = teamMethods.getTeamScore(id, teamName);


        if (score == 0)
            return NotFound();
        else
        {

            return Ok(score);
        }

    }


    //score van een team in een game setten
    [HttpPost]
    [Route("teams/{id}/{teamName}/setmyscore/{newScore}")]
    public IActionResult setTeamScore(int id, string teamName, int newScore)
    {
        //id is de gamecode
        var score = teamMethods.setTeamScore(id,teamName,newScore);

        if (!score)
            return NotFound();
        else
        {
            return Ok(score);
        }

    }


}













//Maybe ever needed-------------------------------------------------------------------------------------------


//get 1 team from a game
//Niet compleet / niet echt relevant meer door de bovenstaande functie imo
//[HttpGet]
//[Route("teams/{id}")]
//public IActionResult getTeam(int id)
//{
//    var team = context.Teams.Find(id);
//    if (team == null)
//        return NotFound();
//    return Ok(team);
//}





//get huidige locatie van een team
//Deze info halen we gewoon uit de all teams from game data
//[HttpGet]
//[Route("teams/{id}/huidigeLocatie")]
//public IActionResult getTeamLocation(int id)
//{
//    var team = context.Teams.Find(id);


//    if (team == null)
//        return NotFound();

//    string cureentLoc = team.HuidigeLong.ToString() + "/" + team.HuidigeLat.ToString();

//    return Ok(cureentLoc);


//}

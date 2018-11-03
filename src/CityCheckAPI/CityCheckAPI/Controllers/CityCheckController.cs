using System;
using System.Collections.Generic;
using System.Linq;
using CityCheckAPI.Controllers.Model;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Model;


[Route("api/citycheck")]
[ApiController]
public class CityCheckController : Controller
{

    private readonly CityCheckContext context;


    public CityCheckController(CityCheckContext context)
    {
        this.context = context;
    }



    //new game
    [HttpPost]
    [Route("newgame")]
    public IActionResult createGame([FromBody] Game newGame)
    {

        IQueryable<Game> query = context.Games;



        //random game code genereren
        string gameCodeStr = "";
        do
        {
            int tempGameCode;
            gameCodeStr = "";
            Random rnd = new Random();
            for (int i = 0; i < 4; i++)
            {
                tempGameCode = rnd.Next(1, 9);
                gameCodeStr += tempGameCode.ToString();
            }
            //controleren of er al een game is met deze code
        } while (query.Where(d => d.GameCode == Int32.Parse(gameCodeStr)) == null);
        newGame.GameCode = Int32.Parse(gameCodeStr);
        //random game code einde


        context.Games.Add(newGame);
        context.SaveChanges();
        return Created("Created:", newGame);
    }



    //get all games
    [HttpGet]
    [Route("allgames")]
    public IActionResult getGames()
    {

        IQueryable<Game> query = context.Games;
        if (query != null)
            return Ok(query);
        else
            return NotFound();
    }




    //get game info
    [HttpGet]
    [Route("currentgame")]
    public IActionResult getGame(int id)
    {
        //id is de code die voor een game aangemaakt wordt.

        var game = context.Games.Find(id);


        if (game == null)
            return NotFound();


        return Ok(game);
    }

    //get teams from game
    [HttpGet]
    [Route ("currentgame/teams")]
    public IActionResult getTeams(int gameId)
    {
        var game = context.Games.Find(gameId);
        if (game == null)
            return NotFound();
        return Ok(game.Teams);
    }

    //new team
    [HttpPost]
    [Route("teams")]
    public IActionResult addteam([FromBody] Team newTeam, int gameId)
    {
        int startBonus = 30;
        newTeam.Punten = startBonus;
        context.Teams.Add(newTeam);

        var game = context.Games.Find(gameId);
        game.Teams.Add(newTeam);

        context.SaveChanges();
        return Created("Created:", newTeam.TeamNaam);
    }

    //get team
    [HttpGet]
    [Route("teams/{id}")]
    public IActionResult getTeam(int id)
    {
        var team = context.Teams.Find(id);
        if (team == null)
            return NotFound();
        return Ok(team);
    }


    //update team
    [HttpPut]
    [Route("teams/{id}")]
    public IActionResult updateTeam([FromBody] Team update)
    {
        var team = context.Teams.Find(update.Id);
        if (team == null)
            return NotFound();
        team = update;
        return Ok(team);
    }

    //delete team
    [HttpDelete]
    [Route("teams/{id}")]
    public IActionResult deleteTeam(int id)
    {
        var team = context.Teams.Find(id);
        if (team == null)
            return NotFound();

        context.Teams.Remove(team);
        context.SaveChanges();
        return NoContent();
    }


    //get huidige locatie van een team
    [HttpGet]
    [Route("teams/{id}/huidigeLocatie")]
    public IActionResult getTeamLocation(int id)
    {
        var team = context.Teams.Find(id);


        if (team == null)
            return NotFound();

        string cureentLoc = team.HuidigeLong.ToString() + "/" + team.HuidigeLat.ToString();

        return Ok(cureentLoc);


    }

    //save teamloc
    [HttpPost]
    [Route("teams/{id}/huidigeLocatie")]
    public IActionResult SaveCurrentTeamLoc(int id, [FromBody] long newlat, long newlong)
    {

        var team = context.Teams.Find(id);


        if (team == null)
            return NotFound();

        team.HuidigeLat = newlat;
        team.HuidigeLong = newlong;



        context.SaveChanges();
        return Created("Created:", newlat+""+newlong);
    }



    //save team color
    [HttpPost]
    [Route("teams/{teamid}/teamcolor")]
    public IActionResult SaveTeamColor(int teamid, [FromBody] string kleurcode)
    {

        var team = context.Teams.Find(teamid);


        if (team == null)
            return NotFound();

        team.Kleur = kleurcode;



        context.SaveChanges();
        return Created("Accepted:", kleurcode);
    }

    //get traces van een team
    /*
    [HttpGet]
    [Route("teams/{id}/trace")]
    public IActionResult getTeamTrace(int id)
    {
        IQueryable<TeamTrace> query = context.Traces;
        query = query.Where(e => e.Team.Equals(id));

        if (query == null)
            return NotFound();
        return Ok(query.ToList());

    }*/



}

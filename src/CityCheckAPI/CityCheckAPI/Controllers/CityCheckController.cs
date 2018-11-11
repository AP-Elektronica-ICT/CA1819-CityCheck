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
    [Route("currentgame/{id}")]
    public IActionResult getGame(int id)
    {
        //id is de code die voor een game aangemaakt wordt.

        Game game = context.Games.Where(d => d.GameCode == id).Include(r=> r.Teams).Single<Game>();

        if (game != null)
            return Ok(game);
        else
            return NotFound();
    }


    //new team in a game
    [HttpPost]
    [Route("teams/{gameId}")]
    public IActionResult addteam([FromBody] Team newTeam, int gameId)
    {
        int startBonus = 30;
        newTeam.Punten = startBonus;

        Game game = context.Games.Where(d => d.GameCode == gameId).Include(r=>r.Teams).Single<Game>();

        if (game != null)
        {
            if (game.Teams == null)
            {
                //Dit is het eerste team
                game.Teams = new List<Team>(new Team[] { newTeam });
                context.SaveChanges();
                return Created("Created:", newTeam.TeamNaam);
            }
            else
            {
                game.Teams.Add(newTeam);

                context.SaveChanges();
                return Created("Created:", newTeam.TeamNaam);
            }
        }
        else
            return NotFound();
        
    }

    //get all teams from a game
    [HttpGet]
    [Route ("currentgame/teams/{gameId}")]
    public IActionResult getTeams(int gameId)
    {
        var game = context.Games.Include(r=>r.Teams).Where(d=>d.GameCode == gameId).Single<Game>();
        List<Team> teams = game.Teams;
        if (game == null)
            return NotFound();
        return Ok(teams);
    }

    

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


    //update team
    //wrss nooit nodig
    [HttpPut]
    [Route("teams/{id}")]
    public IActionResult updateTeam([FromBody] Team update, int id)
    {
        var team = context.Teams.Find(update.Id);
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
        var team = context.Teams.Find(id);
        if (team == null)
            return NotFound();

        context.Teams.Remove(team);
        context.SaveChanges();
        return NoContent();
    }


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

    //save teamloc
    [HttpPost]
    [Route("teams/{id}/{teamname}/huidigeLocatie")]
    public IActionResult SaveCurrentTeamLoc(int id, string teamname, [FromBody] long newlat, long newlong)
    {
        //id is de gamecode
        //we gaan het team selecteren volgens de teamnaam

        Game game = context.Games.Where(d => d.GameCode == id).Include(r=>r.Teams).Single<Game>();
        Team team = game.Teams.Where(d => d.TeamNaam == teamname).Single<Team>();


        if (team == null)
            return NotFound();
        else
        {
            team.HuidigeLat = newlat;
            team.HuidigeLong = newlong;

            context.SaveChanges();
            return Created("Created:", newlat + "" + newlong);
        }
    }



    //save team color
    [HttpPost]
    [Route("teams/{id}/{teamname}/teamcolor")]
    public IActionResult SaveTeamColor(int id, string teamname, [FromBody] int kleurcode)
    {
        //id is de gamecode

        Game game = context.Games.Where(d => d.GameCode == id).Single<Game>();
        Team team = game.Teams.Where(d => d.TeamNaam == teamname).Single<Team>();


        if (team == null)
            return NotFound();
        else
        {
            team.Kleur = kleurcode;

            context.SaveChanges();
            return Created("Accepted:", kleurcode);
        }
    }



    //get all traces of all teams in a game
    
    [HttpGet]
    [Route("teams/{id}/trace")]
    public IActionResult getTeamTrace(int id)
    {
        //id is de gamecode

        Game game = context.Games.Include(r=>r.Teams.Select(x=>x.TeamTraces)).Where(d => d.GameCode == id).Single<Game>();
        List<Team> teams = game.Teams;


        if (teams == null)
            return NotFound();
        else
        {

            return Ok(teams);
        }

    }


}

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
public class GameController : Controller
{

    private readonly CityCheckContext context;

    private GameMethods gameMethods;


    public GameController(CityCheckContext context)
    {
        this.context = context;

        this.gameMethods = new GameMethods(context);


    }



    //Game Controllers

    //new game
    [HttpPost]
    [Route("newgame")]
    public IActionResult createGame([FromBody] Game newGame)
    {

        var returnValue = gameMethods.CreateGame(newGame);
        if (returnValue != null)
            return Created("Created:", returnValue);
        else
            return NotFound();
    }



    //get all games
    [HttpGet]
    [Route("allgames")]
    public IActionResult getGames()
    {

        List<Game> games = gameMethods.GetGames();
        if (games != null)
            return Ok(games);
        else
            return NotFound();
    }




    //get game info
    [HttpGet]
    [Route("currentgame/{id}")]
    public IActionResult getGame(int id)
    {
        //id is de code die voor een game aangemaakt wordt.

        Game game = gameMethods.GetGame(id);

        if (game != null)
            return Ok(game);
        else
            return NotFound();
    }



    //start a game
    [HttpPost]
    [Route("startgame/{id}/{milli}")]
    public IActionResult startGame(double milli, int id)
    {

        //gameid is de gamecode

        var returnValue = gameMethods.startGame(milli, id);
        if (returnValue)
        {
            return Created("Game Started:", milli);
        }
        else
        {
            return NotFound();
        }
    }

    [HttpDelete]
    [Route("currentgame/{id}")]
    public IActionResult deleteGame(int id)
    {
        //id is de gamecode

        bool game = gameMethods.deleteGame(id);
        if (game == false)
            return NotFound();
        return Ok("Deleted");
    }

}

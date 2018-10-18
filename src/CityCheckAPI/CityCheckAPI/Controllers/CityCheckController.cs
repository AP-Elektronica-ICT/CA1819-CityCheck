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

    //new team
    [HttpPost]
    [Route("teams")]
    public IActionResult addteam([FromBody] Team newTeam)
    {
        context.Teams.Add(newTeam);
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
        var team = context.Teams.Find(update.TeamId);
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
    /*
    //get huidige locatie
    [HttpGet]
    [Route("teams/{id}/huidigeLocatie")]
    public IActionResult getTeamLocation(int id)
    {
        var team = context.Teams.Find(id);
        if (team == null)
            return NotFound();
        return Ok(team.HuidigeLocatie);
    }

    //get traces
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

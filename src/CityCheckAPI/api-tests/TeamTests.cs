using DataLayer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using Xunit;

namespace api_tests
{
    public class TeamTests
    {


        TeamController tmController;
        CityCheckContext context;

        public TeamTests()
        {
            var dbOption = new DbContextOptionsBuilder<CityCheckContext>()
    .UseMySql("server=localhost;port=3306;database=CityCheckDB;uid=root;password=12345;")
    .Options;
            context = new CityCheckContext(dbOption);
            tmController = new TeamController(context);
        }



        //TEAM POSTS-----------------------------------------------------------------------

        [Fact]
        public void CreateTeam()
        {

            //Arrange
            Team team = new Team()
            {
                Punten = 30,
                Kleur = 20,
                TeamNaam = "mijnTeam",
                huidigeLocatie = null,
                TeamTraces = null

            };

            int gameID = 3252;


            //Act
            var createdResponse = tmController.addteam(team, gameID);
            var result = createdResponse as OkObjectResult;
            var createdTeam = result.Value as Team;


            //Assert
            Assert.IsType<Team>(createdTeam);

        }




        //Score setten op een team
        [Fact]
        public void setScore()
        {

            //Arrange
            int gameID = 3252;
            string teamName = "mijnTeam";
            int score = 50;


            //Act
            var createdResponse = tmController.setTeamScore(gameID,teamName,score);


            //Assert
            Assert.IsType<OkObjectResult>(createdResponse);

        }


        //Locatie opslaan op een team





        //TEAM POSTS-----------------------------------------------------------------------






        //TEAM GETS-----------------------------------------------------------------------


        [Fact]
        public void GetAllTeamsInGame()
        {

            //Arenge
            int gameid = 3252;

            //Act
            var okResult = tmController.getTeams(gameid);
            var result = okResult as OkObjectResult;
            var teams = result.Value as List<Team>;

            //Assert
            Assert.IsType<List<Team>>(teams);

        }



        [Fact]
        public void GetTeamScore()
        {

            //Arenge
            int gameid = 3252;
            string teamNaam = "mijnTeam";

            //Act
            var okResult = tmController.getTeamScore(gameid,teamNaam);

            //Assert
            Assert.IsType<OkObjectResult>(okResult);

        }



        //TEAM GETS-----------------------------------------------------------------------




    }
}

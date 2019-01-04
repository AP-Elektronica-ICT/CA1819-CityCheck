using DataLayer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using Xunit;

namespace api_tests
{
    public class GameTests
    {

        GameController gmController;
        CityCheckContext context;

        public GameTests()
        {
            var dbOption = new DbContextOptionsBuilder<CityCheckContext>()
    .UseMySql("server=localhost;port=3306;database=CityCheckDB;uid=root;password=12345;")
    .Options;
            context = new CityCheckContext(dbOption);
            gmController = new GameController(context);
        }




        //GAME GETS-----------------------------------------------------------------------


        [Fact]
        public void GetGames()
        {

            //Act
            var okResult = gmController.getGames();

            //Assert
            Assert.IsType<OkObjectResult>(okResult);

        }



        [Fact]
        public void GetGame()
        {

            //Act
            var okResult = gmController.getGame(8341);
            var result = okResult as OkObjectResult;
            var game = result.Value as Game;

            //Assert
            Assert.IsType<Game>(game);

        }





        //GAME GETS-----------------------------------------------------------------------






        //GAME POSTS-----------------------------------------------------------------------


        [Fact]
        public void CreateGame()
        {

            //Arrange
            Game game = new Game()
            {
                hasStarted = true,
                TijdsDuur = 1,
                Teams = null
            };


            //Act
            var createdResponse = gmController.createGame(game);

            //Assert
            Assert.IsType<CreatedResult>(createdResponse);

        }






        [Fact]
        public void StartGame()
        {

            //Arrange
            int gameid = 8341;
            double millis = 16476464;


            //Act
            var createdResponse = gmController.startGame(millis,gameid);

            //Assert
            Assert.IsType<CreatedResult>(createdResponse);

        }









        //GAME POSTS-----------------------------------------------------------------------





        //GAME DELETES-----------------------------------------------------------------------

        [Fact]
        public void DelGame()
        {

            //Arrange
            int gameid = 5337;


            //Act
            var createdResponse = gmController.deleteGame(gameid);

            //Assert
            Assert.IsType<OkObjectResult>(createdResponse);

        }



        //GAME DELETES-----------------------------------------------------------------------
    }
}

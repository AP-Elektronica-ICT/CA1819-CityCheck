using DataLayer;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BussinessLayer.Methods
{
    public class GameMethods
    {

        private CityCheckContext context;

        public GameMethods(CityCheckContext context)
        {
            this.context = context;
        }


        public Game CreateGame(Game newGame, CityCheckContext context)
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
            return newGame;

        }


        public List<Game> GetGames(CityCheckContext context)
        {
            List<Game> query = context.Games.ToList<Game>();
            if (query != null)
                return query;
            else
                return null;
        }



        public Game GetGame(int id, CityCheckContext context)
    {
        Game game = context.Games.Where(d => d.GameCode == id).Include(r => r.Teams).Single<Game>();
        return game;
    }



        public bool startGame(double milli, int gamecode)
        {

            Game game = context.Games.Where(r => r.GameCode == gamecode).Single<Game>();


            if (game != null)
            {
                //game starten
                game.hasStarted = true;
                //gestarte tijd opslaan
                game.millisStarted = milli;
                context.SaveChanges();
                return true;
            }
            else
                return false;

        }

    }


}

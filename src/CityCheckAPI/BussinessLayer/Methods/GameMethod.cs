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


        public Game CreateGame(Game newGame)
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

            //random doellocaties toevoegen aan de game

            List<DoelLocatie> doelen = new List<DoelLocatie>();

            List<DoelLocatie> alleLocs = context.DoelLocaties.ToList<DoelLocatie>();
            int totaalLocs = alleLocs.Count();

            int aantalTeZoekenLocs = newGame.TijdsDuur * 6; //6x10minuten per tijdsduur uren.
            Random rndLocInd = new Random();
            for (int i = 0; i < aantalTeZoekenLocs; i++)
            {
                int newIndex1;
                int newIndex2;
                int newIndex3;
                do
                {
                    //Steeds 3 verschillende locaties
                    newIndex1 = rndLocInd.Next(0, totaalLocs);
                    newIndex2 = rndLocInd.Next(0, totaalLocs);
                    newIndex3 = rndLocInd.Next(0, totaalLocs);
                } while (newIndex1 == newIndex2 || newIndex1 == newIndex3 || newIndex2 == newIndex3);
                
                doelen.Add(alleLocs[newIndex1]);
                doelen.Add(alleLocs[newIndex2]);
                doelen.Add(alleLocs[newIndex3]);
            }

            //Unieke gegenereerde lijst van doellocaties toevoegen aan de gameDoelen van de huidige game.
            newGame.GameDoelen = new List<GameDoelen>();
            foreach (DoelLocatie doelL in doelen)
            {
                GameDoelen tempDoel = new GameDoelen{ Doel = doelL, Claimed = false };
                newGame.GameDoelen.Add(tempDoel);
            }

            //doellocaties toevoegen aan de game
            context.Games.Add(newGame);
            context.SaveChanges();
            return newGame;

        }


        public List<Game> GetGames()
        {
            List<Game> query = context.Games.ToList<Game>();
            if (query != null)
                return query;
            else
                return null;
        }



        public Game GetGame(int id)
    {
        Game game = context.Games.Where(d => d.GameCode == id).Include(r => r.Teams).Include(r=>r.GameDoelen).ThenInclude(doel=>doel.Doel).Single<Game>();
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

        public bool deleteGame(int id)
        {
            Game game = context.Games.Where(d => d.GameCode == id).Include(gm => gm.Teams).ThenInclude(tm => tm.TeamTraces).Include(r => r.GameDoelen).ThenInclude(doel => doel.Doel).SingleOrDefault<Game>();
            if (game == null)
            {
                return false;
            }
            else
            {
                context.Games.Remove(game);
                context.SaveChanges();
                return true;
            }
        }

    }


}

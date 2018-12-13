using DataLayer;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BussinessLayer.Methods
{
    public class TeamMethods
    {

        private CityCheckContext context;

        public TeamMethods(CityCheckContext context)
        {
            this.context = context;
        }


        //methods------------------------------------------------------

        public Team addteam(Team newTeam, int gameId)
        {
            int startBonus = 30;
            newTeam.Punten = startBonus;

            Game game = context.Games.Where(d => d.GameCode == gameId).Include(r => r.Teams).Single<Game>();

            if (game != null)
            {
                if (game.Teams == null)
                {
                    //Dit is het eerste team
                    game.Teams = new List<Team>(new Team[] { newTeam });
                    context.SaveChanges();
                    return newTeam;
                }
                else
                {
                    game.Teams.Add(newTeam);

                    context.SaveChanges();
                    return newTeam;
                }
            }
            else
                return null;
        }



        public List<Team> getTeams(int gameId)
        {
            var game = context.Games.Include(r => r.Teams).Where(d => d.GameCode == gameId).Single<Game>();
            List<Team> teams = game.Teams;
            if (game == null)
                return null;
            else
                return teams;
        }


        public Team updateTeam(Team update, int id)
        {
            var team = context.Teams.Find(update.Id);
            if (team == null)
                return null;
            else
            {
                team = update;
                return update;
            }
        }



        public int deleteTeam(int id)
        {
            var team = context.Teams.Find(id);
            if (team == null)
                return 0;
            else
            {
                context.Teams.Remove(team);
                context.SaveChanges();
                return id;
            }
        }



        public Team SaveCurrentTeamLoc(int id, string teamname, Locatie currentLoc)
        {
            //id is de gamecode
            //we gaan het team selecteren volgens de teamnaam

            Game game = context.Games.Where(r => r.GameCode == id).Include(r => r.Teams).ThenInclude(y=>y.TeamTraces).Single<Game>();
            Team team = game.Teams.Where(d => d.TeamNaam == teamname).Single<Team>();
            List<TeamTrace> traces = team.TeamTraces;

            if (team == null)
                return null;
            else
            {
                //huidige locatie opslaan
                team.huidigeLocatie = currentLoc;

                //nieuwe trace toevoegen
                var trace = new TeamTrace();
                trace.trace = currentLoc;

                if (traces == null)
                {
                    //nog geen trace aanwezig
                    traces = new List<TeamTrace>();
                    traces.Add(trace);
                }
                else
                {
                    traces.Add(trace);
                }
                context.SaveChanges();
                return team;
            }
        }


        public Team SaveTeamColor(int id, string teamname, int kleurcode)
        {
            //id is de gamecode

            Game game = context.Games.Where(d => d.GameCode == id).Single<Game>();
            Team team = game.Teams.Where(d => d.TeamNaam == teamname).Single<Team>();


            if (team == null)
                return null;
            else
            {
                team.Kleur = kleurcode;

                context.SaveChanges();
                return team
;
            }
        }




        public List<Team> getTeamTrace(int id)
        {
            //id is de gamecode

            Game game = context.Games.Include(r => r.Teams).ThenInclude(y=>y.TeamTraces).Where(d => d.GameCode == id).Single<Game>();
            List<Team> teams = game.Teams;


            if (teams == null)
                return null;
            else
            {

                return teams;
            }

        }



        //Dit mag eigenlijk weg
        public bool setTeamTrace(Locatie newTrace, int id, string teamNaam)
        {
            //id is de gamecode

            Game game = context.Games.Include(r => r.Teams).ThenInclude(y => y.TeamTraces).Where(d => d.GameCode == id).Single<Game>();
            Team team = game.Teams.Where(d => d.TeamNaam == teamNaam).Single<Team>();
            List<TeamTrace> traces = team.TeamTraces;



            if (team == null)
                //Geen team
                return false;
            else
            {
                //trace punt toevoegen bij dit team
                var newtrace = new TeamTrace();
                newtrace.trace = newTrace;
                traces.Add(newtrace);
                context.SaveChanges();
                return true;
            }

        }


        public int getTeamScore(int id, string teamName)
        {
            //id is de gamecode

            Game game = context.Games.Include(r => r.Teams).Where(d => d.GameCode == id).Single<Game>();
            Team team = game.Teams.Where(r => r.TeamNaam == teamName).Single<Team>();
            int score = team.Punten;


            if (team == null)
                return 0;
            else
            {

                return score;
            }

        }



        public bool setTeamScore(int id, string teamName, int newScore)
        {
            //id is de gamecode

            Game game = context.Games.Include(r => r.Teams).Where(d => d.GameCode == id).Single<Game>();
            Team team = game.Teams.Where(r => r.TeamNaam == teamName).Single<Team>();
            int score = team.Punten;


            if (team == null)
                return false;
            else
            {
                score = newScore;
                context.SaveChanges();
                return true;
            }

        }






    }


}

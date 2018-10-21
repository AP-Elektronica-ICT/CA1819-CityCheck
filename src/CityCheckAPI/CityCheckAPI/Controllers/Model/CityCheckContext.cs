using CityCheckAPI.Controllers.Model;
using Microsoft.EntityFrameworkCore;

namespace Model
{
    public class CityCheckContext : DbContext
    {

        public CityCheckContext(DbContextOptions<CityCheckContext> options) : base(options)
        {

        }

        //public DbSet<Class> Classnaam { get; set; }

        //......

        public DbSet<Game> Games { get; set; }

        public DbSet<DoelLocatie> DoelLocaties { get; set; }

        public DbSet<Vraag> Vragen { get; set; }

        public DbSet<Antwoord> Antwoorden { get; set; }

    
        public DbSet<Team> Teams { get; set; }

        public DbSet<TeamTrace> TeamTraces { get; set; }


    }







}
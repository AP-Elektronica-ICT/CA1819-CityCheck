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

        public DbSet<Team> Teams { get; set; }
        public DbSet<TeamTrace> Traces {get; set;}
        public DbSet<Locatie> Locaties { get; set; }
    }







}
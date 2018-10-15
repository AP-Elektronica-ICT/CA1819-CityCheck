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


    }







}
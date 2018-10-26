using CityCheckAPI.Controllers.Model;
using System.Collections.Generic;

namespace Model
{
    public class Game
    {
        public int Id { get; set; }
        public int GameCode { get; set; }
        public int TijdsDuur { get; set; }
        public ICollection<Team> Teams { get; set; }

    }
}


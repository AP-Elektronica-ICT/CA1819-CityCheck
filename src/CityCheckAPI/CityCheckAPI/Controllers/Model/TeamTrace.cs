using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CityCheckAPI.Controllers.Model
{
    public class TeamTrace
    {
        public int IdTeamTrace { get; set; }
        public List<Locatie> Locaties { get; set; }
    }
}

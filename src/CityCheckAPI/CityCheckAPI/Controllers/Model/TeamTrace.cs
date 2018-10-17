using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace CityCheckAPI.Controllers.Model
{
    public class TeamTrace
    {
        public int TeamTraceId { get; set; }
        [ForeignKey("TeamId")]
        public virtual Team Team { get; set; }
        public List<Locatie> Locaties { get; set; }
    }
}

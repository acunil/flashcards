import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../../contexts";

const LandingWrapper = () => {
  const navigate = useNavigate();
  const { subjects } = useAppContext();

  useEffect(() => {
    if (subjects.length === 0) {
      navigate("/subjects");
    } else {
      navigate("/home");
    }
  }, [subjects, navigate]);

  return null; // or a loading placeholder if needed
};

export default LandingWrapper;
